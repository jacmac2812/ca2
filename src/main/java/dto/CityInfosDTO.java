/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.CityInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jacobsimonsen
 */
public class CityInfosDTO {
        List<CityInfoDTO> all = new ArrayList();

    public CityInfosDTO(List<CityInfo> cityInfoEntities) {
        cityInfoEntities.forEach((ci) -> {
            all.add(new CityInfoDTO(ci));
        });
    }

    public List<CityInfoDTO> getAll() {
        return all;
    }
}
