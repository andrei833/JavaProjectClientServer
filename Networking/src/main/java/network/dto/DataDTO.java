package network.dto;

public class DataDTO {
    private ListDTO<ParticipantDTO> participants;
    private ListDTO<RaceDTO> races;
    private ListDTO<RegistrationDTO> registrations;

    public DataDTO() {
    }

    public DataDTO(ListDTO<ParticipantDTO> participants, ListDTO<RaceDTO> races, ListDTO<RegistrationDTO> registrations) {
        this.participants = participants;
        this.races = races;
        this.registrations = registrations;
    }

    public ListDTO<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(ListDTO<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public ListDTO<RaceDTO> getRaces() {
        return races;
    }

    public void setRaces(ListDTO<RaceDTO> races) {
        this.races = races;
    }

    public ListDTO<RegistrationDTO> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(ListDTO<RegistrationDTO> registrations) {
        this.registrations = registrations;
    }
}
