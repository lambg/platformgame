package platformer.world.entity;

public class LivingEntity extends Entity {

    private int maxHealth;
    private int currentHealth;

    public LivingEntity(int health) {
        super();
        this.maxHealth = health;
        currentHealth = maxHealth;
    }

    public int getHealth() {
        return currentHealth;
    }

    public void increaseHealth(int value) {
        currentHealth += value;

    }

    public void increaseHealth() {
        currentHealth++;
    }

    public void decreaseHealth(int value) {
        currentHealth -= value;
    }

    public void decreaseHealth() {
        currentHealth--;
    }



}
