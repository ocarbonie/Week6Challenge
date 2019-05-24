package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CarRepository carRepository;
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String showAllCars(Model model){
        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "index";
    }
    @GetMapping("/addCategory")
    public String catForm(Model model){
        model.addAttribute("category", new Category());
        return "categoryForm";
    }
    @PostMapping("/processCategory")
    public String processCatForm(Category category, Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        categoryRepository.save(category);
        return"redirect:/";
    }
    @GetMapping("/addCar")
    public String carForm(Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("car", new Car());
        return "carForm";
    }
    @PostMapping("/processCar")
    public String processCarForm(@ModelAttribute Car car, @Valid @RequestParam("file")MultipartFile file, Model model){
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("cars" ,carRepository.findAll());
        if(file.isEmpty()){
            return "redirect:/addCar";
        }
        try{
            Map uploadResult= cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            car.setPic(uploadResult.get("url").toString());
            carRepository.save(car);
        }catch(IOException e) {
            e.printStackTrace();
            return "redirect:/addCar";
        }


        return "redirect:/";
    }
    @RequestMapping("/detail/{id}")
    public String showCar(@PathVariable("id") long id, Model model){
        model.addAttribute("car", carRepository.findById(id).get());
        model.addAttribute("categories", categoryRepository.findAll());
        return "showCar";
    }
    @RequestMapping("/update/{id}")
    public String updateCar(@PathVariable("id") long id, Model model){
        model.addAttribute("car", carRepository.findById(id).get());
        model.addAttribute("categories", categoryRepository.findAll());
        return "carForm";
    }
    @RequestMapping("/delete/{id}")
        public String deleteCar(@PathVariable("id") long id){
        carRepository.deleteById(id);
        return "redirect:/";
        }

}
