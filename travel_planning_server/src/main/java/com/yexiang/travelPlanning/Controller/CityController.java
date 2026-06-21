package com.yexiang.travelPlanning.Controller;

import com.yexiang.travelPlanning.Entity.City;
import com.yexiang.travelPlanning.service.CityService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 城市详情
 */
@RestController
@RequestMapping("city")
public class CityController {
    @Resource
    private CityService cityService;

    //查询所有城市名
    @GetMapping("/getAllCity")
    public List<String> selectAllCity() {
        List<City> cityList = cityService.list();
        if(cityList.isEmpty()) {
            throw new NullPointerException();
        }
        List<String> cityName = cityList.stream().map(City::getName).collect(Collectors.toList());
        return cityName;
    }
}
