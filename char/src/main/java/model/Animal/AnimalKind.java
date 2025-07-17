package model.Animal;

import javax.swing.plaf.synth.SynthRadioButtonMenuItemUI;
import java.util.HashMap;
import java.util.Map;

public enum AnimalKind {
    COW("cow", "barn", 4, 1500, Map.of(
            AnimalProduct.MILK, 125,
            AnimalProduct.LARGE_MILK, 190)),

    GOAT("goat", "barn", 8, 4000, Map.of(
            AnimalProduct.GOAT_MILK, 225,
            AnimalProduct.LARGE_GOATMILK, 345)),

    SHEEP("sheep", "barn", 12, 8000, Map.of(
            AnimalProduct.SHEEP_WOOL, 340)),

    PIG("pig", "barn", 4, 16000, Map.of(
            AnimalProduct.TRUFFLE, 625)),

    CHICKEN("chicken", "coop", 4, 800, Map.of(
            AnimalProduct.EGG, 50,
            AnimalProduct.LARGE_EGG, 95)),

    DUCK("duck", "coop", 8, 1200, Map.of(
            AnimalProduct.DUCK_EGG, 95,
            AnimalProduct.DUCK_FEATHER, 250)),

    RABBIT("rabbit", "coop", 12, 8000, Map.of(
            AnimalProduct.RABBITE_WOOL, 340,
            AnimalProduct.RABBIT_FOOT, 565)),

    DINOSAUR("dinosaur", "coop", 8, 14000, Map.of(
            AnimalProduct.DINOSAUR_EGG, 350));

    ;
    private final String type;
    private final String animalKind;
    private final int capacityOfHome;
    private final int priceOfBuy;
    private final Map<AnimalProduct,Integer> productPrices;


    AnimalKind(String type, String animalKind, int capacityOfHome, int priceOfBuy, Map<AnimalProduct,Integer> productPrices) {
        this.type = type;
        this.capacityOfHome = capacityOfHome;
        this.animalKind = animalKind;
        this.priceOfBuy = priceOfBuy;
        this.productPrices = productPrices;

    }
    public int getPriceOfBuy() {
        return priceOfBuy;
    }


    public Map<AnimalProduct, Integer> getProductPrices() {
        return productPrices;
    }


    public String getAnimalKind() {
        return animalKind;

    }
}
