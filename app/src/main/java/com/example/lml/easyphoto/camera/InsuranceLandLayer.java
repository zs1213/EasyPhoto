package com.example.lml.easyphoto.camera;

import com.esri.arcgisruntime.layers.FeatureLayer;

public class InsuranceLandLayer implements Featurable {

    public InsuranceLandLayer(String shapePath) {
        this.shapePath = shapePath;
    }

    @Override
    public FeatureLayer buildLayer() {
        try {
//            FeatureLayer featureLayer = new FeatureLayer(new ShapefileFeatureTable(this.shapePath));
            //featureLayer.setRenderer(new SimpleRenderer(new SimpleLineSymbol(Color.GREEN, 1f)));
//            return featureLayer;
        }catch(Exception ex){
        }
        return null;
    }
    private String shapePath = "";
}
