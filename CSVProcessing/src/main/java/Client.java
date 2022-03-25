import java.util.ArrayList;

public class Client {
    private int number;
    private String first_name;
    private String last_name;
    private String company;
    private String address;
    private String city;
    private String county;
    private String state;
    private int postal_code;
    private double risk_factor;
    private String phone1;
    private String phone2;
    private String email;
    private String web;

    @Override
    public String toString() {
        return "Client{" +
                "number=" + number +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", company='" + company + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", state='" + state + '\'' +
                ", postal_code=" + postal_code +
                ", risk_factor=" + risk_factor +
                ", phone1='" + phone1 + '\'' +
                ", phone2='" + phone2 + '\'' +
                ", email='" + email + '\'' +
                ", web='" + web + '\'' +
                '}';
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(int postal_code) {
        this.postal_code = postal_code;
    }

    public double getRisk_factor() {
        return risk_factor;
    }

    public void setRisk_factor(int risk_factor) {
        this.risk_factor = risk_factor;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Client(){}

    public Client(String[] csvData) {
        this.number = Integer.parseInt(csvData[0]);
        this.first_name = csvData[1];
        this.last_name = csvData[2];
        this.company = csvData[3];
        this.address = csvData[4];
        this.city = csvData[5];
        this.county = csvData[6];
        this.state = csvData[7];
        this.postal_code = Integer.parseInt(csvData[8]);
        this.risk_factor = Double.parseDouble(csvData[9]);
        this.phone1=csvData[10];
        this.phone2=csvData[11];
        this.email = csvData[12];
        this.web = csvData[13];
    }


}
