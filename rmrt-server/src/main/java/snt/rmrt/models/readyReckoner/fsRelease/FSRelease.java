package snt.rmrt.models.readyReckoner.fsRelease;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class FSRelease {

    @Column(name = "_release")
    @Id private String release;
    private String majorRelease;
    private String rState;


    @JsonSetter("Release")
    public void setRelease(String release) {
        this.release = release;
    }


    @JsonSetter("Major Release")
    public void majorRelease(String majorRelease) {
        this.majorRelease = majorRelease;
    }


    @JsonSetter("rstate")
    public void setrState(String rState) {
        this.rState = rState;
    }
}
