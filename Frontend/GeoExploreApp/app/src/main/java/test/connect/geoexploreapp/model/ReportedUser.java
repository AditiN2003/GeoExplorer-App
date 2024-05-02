package test.connect.geoexploreapp.model;

public class ReportedUser {
    private Long id;
    private Long reportedUserId;
    private Boolean harassment;
    private Boolean misinformation;
    private Boolean spamming;
    private Boolean inappropriateContent;



    public ReportedUser(Long userId, Boolean harass, Boolean misInfo, Boolean spam, Boolean inappropriate){

        this.reportedUserId = userId;
        this.harassment = harass;
        this.misinformation = misInfo;
        this.spamming = spam;
        this.inappropriateContent = inappropriate;

    }

    public ReportedUser(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public Boolean getHarassment() {
        return harassment;
    }

    public void setHarassment(Boolean harassment) {
        this.harassment = harassment;
    }

    public Boolean getMisinformation() {
        return misinformation;
    }

    public void setMisinformation(Boolean misinformation) {
        this.misinformation = misinformation;
    }

    public Boolean getSpamming() {
        return spamming;
    }

    public void setSpamming(Boolean spamming) {
        this.spamming = spamming;
    }

    public Boolean getInappropriateContent() {
        return inappropriateContent;
    }

    public void setInappropriateContent(Boolean inappropriateContent) {
        this.inappropriateContent = inappropriateContent;
    }

}
