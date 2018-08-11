package io.anuke.ld42.entities.traits;

public interface LayerTrait{

    default Layer getLayer(){
        return Layer.sorted;
    }

    enum Layer{
        sorted, bloom
    }
}
