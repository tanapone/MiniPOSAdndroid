package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class CategoryModel {
    @Expose
    private Category category;

    public CategoryModel(){}

    public CategoryModel(String jsonResponse){
        Gson gson  = new GsonBuilder().create();
        category = gson.fromJson(jsonResponse,Category.class);
    }
    public Category getCategory() {
        return category;
    }


    public class Category{
        @Expose
      private long id;
        @Expose
      private String categoryName;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }


}
