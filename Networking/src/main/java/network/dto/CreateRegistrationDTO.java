package network.dto;

public class CreateRegistrationDTO {
    private String participantName;
    private int participantAge;
    private Integer raceId;


    public CreateRegistrationDTO() {
    }

    public CreateRegistrationDTO(String participantName, int participantAge, Integer raceId) {
        this.participantName = participantName;
        this.participantAge = participantAge;
        this.raceId = raceId;
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

    public Integer getRaceId() {
        return raceId;
    }

    public void setRaceId(Integer registrationId) {
        this.raceId = registrationId;
    }

    @Override
    public String toString() {
        return "CreateRegistrationDTO{" +
                "participantName='" + participantName + '\'' +
                ", participantAge=" + participantAge +
                ", raceId=" + raceId +
                '}';
    }

}
