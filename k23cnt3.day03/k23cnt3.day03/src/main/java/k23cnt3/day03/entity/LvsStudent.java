package k23cnt3.day03.entity;

public class LvsStudent {
    private Long lvsId;
    private String lvsName;
    private int lvsAge;
    private String lvsGender;
    private String lvsAddress;
    private String lvsPhone;
    private String lvsEmail;

    public LvsStudent() {
    }

    public LvsStudent(Long lvsId, String lvsName, int lvsAge, String lvsGender,
                      String lvsAddress, String lvsPhone, String lvsEmail) {
        this.lvsId = lvsId;
        this.lvsName = lvsName;
        this.lvsAge = lvsAge;
        this.lvsGender = lvsGender;
        this.lvsAddress = lvsAddress;
        this.lvsPhone = lvsPhone;
        this.lvsEmail = lvsEmail;
    }

    // Sửa thành getter/setter chuẩn
    public Long getLvsId() {
        return lvsId;
    }

    public void setLvsId(Long lvsId) {
        this.lvsId = lvsId;
    }

    public String getLvsName() {
        return lvsName;
    }

    public void setLvsName(String lvsName) {
        this.lvsName = lvsName;
    }

    public int getLvsAge() {
        return lvsAge;
    }

    public void setLvsAge(int lvsAge) {
        this.lvsAge = lvsAge;
    }

    public String getLvsGender() {
        return lvsGender;
    }

    public void setLvsGender(String lvsGender) {
        this.lvsGender = lvsGender;
    }

    public String getLvsAddress() {
        return lvsAddress;
    }

    public void setLvsAddress(String lvsAddress) {
        this.lvsAddress = lvsAddress;
    }

    public String getLvsPhone() {
        return lvsPhone;
    }

    public void setLvsPhone(String lvsPhone) {
        this.lvsPhone = lvsPhone;
    }

    public String getLvsEmail() {
        return lvsEmail;
    }

    public void setLvsEmail(String lvsEmail) {
        this.lvsEmail = lvsEmail;
    }
}