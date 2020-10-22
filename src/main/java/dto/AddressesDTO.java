/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Address;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fh
 */
public class AddressesDTO {

    List<AddressDTO> all = new ArrayList();

    public AddressesDTO(List<Address> addressEntities) {
        addressEntities.forEach((a) -> {
            all.add(new AddressDTO(a));
        });
    }

    public List<AddressDTO> getAll() {
        return all;
    }
}
