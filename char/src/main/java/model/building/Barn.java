package model.building;

import model.game.Position;
import model.Animal.Animal;

import java.util.ArrayList;
import java.util.List;

public class Barn extends Buildings {

    private final BarnType barnType;
    private final List<Animal> animals;
    private static int capacity;


    public Barn(BarnType barnType, Position topLeft) {
        super(barnType.name(), topLeft, barnType.getWidth(), barnType.getHeight());
        this.barnType = barnType;
        this.animals = new ArrayList<>();
    }

    public BarnType getBarnType() {
        return barnType;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void addAnimal(Animal animal) {
        capacity--;
        animals.add(animal);
    }

    public boolean removeAnimal(Animal animal) {
        return animals.remove(animal);
    }

    public static void getType(){

    }

    @Override
    public void interact() {
    }

    @Override
    public String toString() {
        return barnType.toString();
    }
}
