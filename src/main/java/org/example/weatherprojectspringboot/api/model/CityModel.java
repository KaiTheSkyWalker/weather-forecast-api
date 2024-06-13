package org.example.weatherprojectspringboot.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CityModel {
    private List<CityProvinceModel> 城市代码;

    @Getter
    @Setter
    public static class CityProvinceModel {
        private String 省;
        private List<CityPostalCode> 市;

        @Getter
        @Setter
        public static class CityPostalCode {
            private String 市名;
            private String 编码;
        }
    }
}
