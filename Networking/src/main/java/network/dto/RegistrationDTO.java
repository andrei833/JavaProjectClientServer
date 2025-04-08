package network.dto;

public class RegistrationDTO {
    private String name;
    private int age;
    private int raceId;

    public RegistrationDTO() {
    }

    public RegistrationDTO(String name, int age, int raceId) {
        this.name = name;
        this.age = age;
        this.raceId = raceId;
    }


    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getRaceId() {
        return raceId;
    }
}