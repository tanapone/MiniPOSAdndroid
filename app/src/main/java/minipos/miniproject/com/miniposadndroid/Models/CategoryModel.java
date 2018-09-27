package minipos.miniproject.com.miniposadndroid.Models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CategoryModel {

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
      private long id;
      private String categoryName;
    }


}
