package com.example.demo.models;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    private String courseContentRating;
    private String instructionQualityRating;
    private String trainingExperienceRating;
    private String textBookRating;
    private String classroomEnvironment;
    private String equipmentRating;

    private String whatDidYouLike;
    private String whatDidntYouLike;
    private String whatImprovements;
    private String whatOtherClasses;

    private String howDidYouFindOut;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCourseContentRating() {
        return courseContentRating;
    }

    public void setCourseContentRating(String courseContentRating) {
        this.courseContentRating = courseContentRating;
    }

    public String getInstructionQualityRating() {
        return instructionQualityRating;
    }

    public void setInstructionQualityRating(String instructionQualityRating) {
        this.instructionQualityRating = instructionQualityRating;
    }

    public String getTrainingExperienceRating() {
        return trainingExperienceRating;
    }

    public void setTrainingExperienceRating(String trainingExperienceRating) {
        this.trainingExperienceRating = trainingExperienceRating;
    }

    public String getTextBookRating() {
        return textBookRating;
    }

    public void setTextBookRating(String textBookRating) {
        this.textBookRating = textBookRating;
    }

    public String getClassroomEnvironment() {
        return classroomEnvironment;
    }

    public void setClassroomEnvironment(String classroomEnvironment) {
        this.classroomEnvironment = classroomEnvironment;
    }

    public String getEquipmentRating() {
        return equipmentRating;
    }

    public void setEquipmentRating(String equipmentRating) {
        this.equipmentRating = equipmentRating;
    }

    public String getWhatDidYouLike() {
        return whatDidYouLike;
    }

    public void setWhatDidYouLike(String whatDidYouLike) {
        this.whatDidYouLike = whatDidYouLike;
    }

    public String getWhatDidntYouLike() {
        return whatDidntYouLike;
    }

    public void setWhatDidntYouLike(String whatDidntYouLike) {
        this.whatDidntYouLike = whatDidntYouLike;
    }

    public String getWhatImprovements() {
        return whatImprovements;
    }

    public void setWhatImprovements(String whatImprovements) {
        this.whatImprovements = whatImprovements;
    }

    public String getWhatOtherClasses() {
        return whatOtherClasses;
    }

    public void setWhatOtherClasses(String whatOtherClasses) {
        this.whatOtherClasses = whatOtherClasses;
    }

    public String getHowDidYouFindOut() {
        return howDidYouFindOut;
    }

    public void setHowDidYouFindOut(String howDidYouFindOut) {
        this.howDidYouFindOut = howDidYouFindOut;
    }
}
