package axon.statistics.domain;

public class Applicant {
    private String[] fullName;
    private String email;

    public Applicant(String[] fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFirstName() {
        return fullName[0];
    }

    public String getLastName() {
        return fullName[fullName.length - 1];
    }

    public String getMiddleNames() {
        StringBuilder stringBuilder = new StringBuilder();

        if (fullName.length > 2) {
            int i = 1;
            for (i = 1; i < fullName.length - 2; i++) {
                stringBuilder.append(fullName[i]);
                stringBuilder.append(" ");
            }
            stringBuilder.append(fullName[i]);
        }

        return stringBuilder.toString();
    }

    public String getFullName() {
        StringBuilder stringBuilder = new StringBuilder();

        for (String name : fullName) {
            stringBuilder.append(name);
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String[] fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
