package br.com.instapromo.instapromo.model;

import java.util.List;

/**
 * Created by joao on 24/06/16.
 */
public class Image {

    private List<ImgUrlData> imgUrlImgUrlData;

    public List<ImgUrlData> getImgUrlImgUrlData() {
        return imgUrlImgUrlData;
    }

    public String imgUrlLink(){
        return imgUrlImgUrlData.get(0).getLink();
    }
}
