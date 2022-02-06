package com.example.tinkoffproject;

import android.media.Image;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ImageLoader {
    private abstract static class ImageCategory {
        private final ArrayList<String> imageCache = new ArrayList<>();
        private int iter = 0;
        private final String link;

        public ImageCategory(String link) {
            this.link = link;
        }

        public String getPageLink() {
            return link;
        }

        public void loadImages(String[] startLinks) {
            imageCache.addAll(Arrays.asList(startLinks));
        }

        public boolean hasNext() {
            return iter < imageCache.size() - 1;
        }

        public boolean hasPrevious() {
            return iter > 0;
        }

        public String getCurrent() {
            if (isEmpty())
                throw new ArrayIndexOutOfBoundsException("ImageCategory is empty");
            return imageCache.get(iter);
        }

        public String getNext() {
            if (!hasNext())
                throw new ArrayIndexOutOfBoundsException("ImageCategory has not next.xml element");
            return imageCache.get(++iter);
        }

        public boolean isEmpty() {
            return imageCache.isEmpty();
        }

        public String getPrevious() {
            if (!hasPrevious())
                throw new ArrayIndexOutOfBoundsException("ImageCategory has not previous element");
            return imageCache.get(--iter);
        }
    }

    private static class CountImageCategory extends ImageCategory {
        private int currentPage = 0;


        public CountImageCategory(String category) {
            super("https://developerslife.ru/" + category + "/%d?json=true");
        }

        @Override
        public String getPageLink(){
            return String.format(super.getPageLink(), currentPage);
        }
        @Override
        public void loadImages(String[] links){
            super.loadImages(links);
            currentPage++;
        }
    }

    private static class RandomImageCategory extends ImageCategory {
        public RandomImageCategory(String link) {
            super(link);
        }
    }

    public enum CategoryType {
        LATEST,
        TOP,
        HOT
    }

    private final ImageCategory latest = new CountImageCategory("latest");
    private final ImageCategory random = new RandomImageCategory("https://developerslife.ru/random?json=true");
    private final ImageCategory top = new CountImageCategory("top");
    private ImageCategory currentCategory = latest;


    public void showNextImage(ImageView image) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!currentCategory.hasNext()) {
                    loadNextImages(currentCategory);
                }
                loadImageToGlide(image, currentCategory.getNext());
            }
        }).start();

    }

    public void showPreviousImage(ImageView image) {
        if (currentCategory.hasPrevious()) {
            loadImageToGlide(image, currentCategory.getPrevious());
        }
    }

    public void showCurrentImage(ImageView image) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (currentCategory.isEmpty()) {
                    loadNextImages(currentCategory);
                }
                loadImageToGlide(image, currentCategory.getCurrent());
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void loadImageToGlide(ImageView image, String link) {
        image.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(image.getContext()).load(link).into(image);
            }
        });
    }

    public void switchCategory(CategoryType type) {
        if (type == CategoryType.LATEST)
            currentCategory = latest;
        else if (type == CategoryType.TOP)
            currentCategory = top;
        else
            currentCategory = random;
    }

    private void loadNextImages(ImageCategory category) {
        try {
            String pageText = new ContentLoader(category.getPageLink()).getContent();
            JSONObject json = new JSONObject(pageText);
            if (json.has("result")){
                JSONArray arr = json.getJSONArray("result");
                String[] links = new String[json.length()];
                for (int i = 0; i < links.length; i++) {
                    links[i] = arr.getJSONObject(i).getString("gifURL");
                }
                category.loadImages(links);
            } else {
                category.loadImages(new String[] { json.getString("gifURL") });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
