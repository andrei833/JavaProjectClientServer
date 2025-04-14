package network.dto;

public class CreateRegistrationDTO {
    private String participantName;
    private int participantAge;
    private Integer registrationId;


    public CreateRegistrationDTO() {
    }

    public CreateRegistrationDTO(String participantName, int participantAge, Integer registrationId) {
        this.participantName = participantName;
        this.participantAge = participantAge;
        this.registrationId = registrationId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public int getParticipantAge() {
        return participantAge;
    }

    public void setParticipantAge(int participantAge) {
        this.participantAge = participantAge;
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }
}
