package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class CompanyModel {
    @Expose
    private Company company;

    public CompanyModel(){}

    public Company getCompany(){
        return this.company;
    }

    public CompanyModel(String jsonResponse){
        Gson gson  = new GsonBuilder().create();
        company = gson.fromJson(jsonResponse,Company.class);
    }


    public class Company{
        @Expose
        private long id;
        @Expose
        private String companyName;
        @Expose
        private String companyPhoneNumber;
        @Expose
        private String companyEmail;
        @Expose
        private String companyAddress;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyPhoneNumber() {
            return companyPhoneNumber;
        }

        public void setCompanyPhoneNumber(String companyPhoneNumber) {
            this.companyPhoneNumber = companyPhoneNumber;
        }

        public String getCompanyEmail() {
            return companyEmail;
        }

        public void setCompanyEmail(String companyEmail) {
            this.companyEmail = companyEmail;
        }

        public String getCompanyAddress() {
            return companyAddress;
        }

        public void setCompanyAddress(String companyAddress) {
            this.companyAddress = companyAddress;
        }
    }
}
