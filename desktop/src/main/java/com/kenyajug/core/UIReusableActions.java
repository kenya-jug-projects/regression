package com.kenyajug.core;
import javafx.scene.image.Image;
public class UIReusableActions {
    public static Image loadImage(String resName) throws ImageNotFoundException {
        var resource = UIReusableActions.class
                .getResource("/images/" + resName)
                .toExternalForm();
        if (resource == null)
         throw new ImageNotFoundException();
        else
            return new Image(resource);
    }
}
