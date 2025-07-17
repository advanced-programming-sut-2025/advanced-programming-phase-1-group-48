package model.building;

import model.game.Position;
import model.Animal.Animal;

import java.util.ArrayList;
import java.util.List;

public class Coop extends Buildings {

    private final CoopType coopType;
    private final List<Animal> animals;
    private static int capacity;

    public Coop(CoopType coopType, Position topLeft) {
        super(coopType.name(), topLeft, coopType.getWidth(), coopType.getHeight());
        this.coopType = coopType;
        this.animals = new ArrayList<>();
        Coop.capacity = coopType.getCapacity();
    }

    public CoopType getCoopType() {
        return coopType;
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

    @Override
    public void interact() {
    }

    @Override
    public String toString() {
        return coopType.toString();
    }
}
