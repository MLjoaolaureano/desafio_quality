package com.desafios.desafio_quality.service;

import com.desafios.desafio_quality.entity.District;
import com.desafios.desafio_quality.entity.Property;
import com.desafios.desafio_quality.entity.Room;
import com.desafios.desafio_quality.exception.PropertyNotFoundException;
import com.desafios.desafio_quality.repository.DistrictRepository;
import com.desafios.desafio_quality.repository.PropertyRepository;
import com.desafios.desafio_quality.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @InjectMocks
    PropertyService propertyService;

    @Mock
    PropertyRepository propertyRepository;

    @Mock
    RoomRepository roomRepository;

    @Mock
    DistrictRepository districtRepository;

    @BeforeEach
    void setup() {

    }

    @Test
    void testSave() {
        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room("Bedroom", 8.00, 2.50));
        roomList.add(new Room("Kitchen", 10.00, 3.75));
        roomList.add(new Room("Bathroom", 4.50, 3.00));

        District district = new District(1L, "District Test", new BigDecimal(97.00));

        Property property = new Property(1L, "Property Test", district,
                roomList);

        Mockito.when(districtRepository.save(ArgumentMatchers.any()))
                .thenReturn(district);

        Mockito.when(propertyRepository.save(ArgumentMatchers.any()))
                .thenReturn(property);

        Mockito.when(roomRepository.saveAll(ArgumentMatchers.any()))
                .thenReturn(roomList);

        Property savedProperty = propertyService.save(property);

        assertEquals(property, savedProperty);
        ArgumentCaptor<District> districtArgumentCaptor = ArgumentCaptor.forClass(District.class);
        Mockito.verify(districtRepository).save(districtArgumentCaptor.capture());

        ArgumentCaptor<Property> propertyArgumentCaptor = ArgumentCaptor.forClass(Property.class);
        Mockito.verify(propertyRepository).save(propertyArgumentCaptor.capture());

        ArgumentCaptor<List<Room>> roomListArgumentCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(roomRepository).saveAll(roomListArgumentCaptor.capture());

    }

    @Test
    void testFindById() {

        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room("Bedroom", 8.00, 2.50));
        roomList.add(new Room("Kitchen", 10.00, 3.75));
        roomList.add(new Room("Bathroom", 4.50, 3.00));

        District district = new District(1L, "District Test", new BigDecimal(97.00));

        Property property = new Property(1L, "Property Test", district,
                roomList);

        Mockito.when(propertyRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.of(property));

        Property savedProperty = propertyService.findById(1L);

        assertEquals(savedProperty, property);
        Mockito.verify(propertyRepository).findById(1L);

    }

    @Test
    @DisplayName("Calculate the total area of the property")
    void getTotalM2PropertyById_returnTotalM2_whenSucess() {
        Double totalM2Expected = 71.0;
        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room("Bedroom", 8.00, 2.50));
        roomList.add(new Room("Kitchen", 10.00, 3.75));
        roomList.add(new Room("Bathroom", 4.50, 3.00));

        Property property = new Property(1L, "Property Test",
                new District(1L, "District Test", new BigDecimal(97.00)),
                roomList);

        Mockito.when((roomRepository.findByProperty(ArgumentMatchers.any())))
                .thenReturn(roomList);

        Mockito.when(propertyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(property));

        Double total = propertyService.getTotalM2PropertyById(property.getId());

        assertThat(total).isEqualTo(totalM2Expected);
    }

    @Test
    @DisplayName("Throw Exception PropertyNotFoundException")
    void getTotalM2PropertyById_returnPropertyNotFoundException_whenIdPropertyInvalid() {
        Long idNotFound = -1L;

        BDDMockito.given(propertyRepository.findById(ArgumentMatchers.anyLong()))
                .willThrow(new PropertyNotFoundException(idNotFound));

        assertThrows(PropertyNotFoundException.class, () -> {
            propertyService.getTotalM2PropertyById(idNotFound);
        });
    }

    @Test
    @DisplayName("Return property price by id")
    void getPricePropertyById_returnPrice_whenSucess() {
        BigDecimal result = new BigDecimal(6887);
        List<Room> roomList = new ArrayList<>();
        roomList.add(new Room("Bedroom", 8.00, 2.50));
        roomList.add(new Room("Kitchen", 10.00, 3.75));
        roomList.add(new Room("Bathroom", 4.50, 3.00));

        Property property = new Property(1L, "Property Test",
                new District(1L, "District Test", new BigDecimal(97.00)),
                roomList);

        Mockito.when((roomRepository.findByProperty(ArgumentMatchers.any())))
                .thenReturn(roomList);

        Mockito.when(propertyRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(property));

        BigDecimal price = propertyService.pricePropertyById(property.getId());
        assertThat(price).isEqualTo(result);
    }

    @Test
    @DisplayName("Throw Exception with inválid id in pricePropertyById")
    void getPricePropertyById_returnPropertyNotFoundException_whenPassInvalidId() {
        Long idNotFound = -1L;

        BDDMockito.given(propertyRepository.findById(ArgumentMatchers.anyLong()))
                .willThrow(new PropertyNotFoundException(idNotFound));

        assertThrows(PropertyNotFoundException.class, () -> {
            propertyService.pricePropertyById(idNotFound);
        });
    }
}