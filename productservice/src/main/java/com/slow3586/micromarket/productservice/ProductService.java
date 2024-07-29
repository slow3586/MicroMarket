package com.slow3586.micromarket.productservice;


import com.slow3586.micromarket.api.OrderDto;
import com.slow3586.micromarket.api.ProductDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;

    public OrderDto processOrder(OrderDto order) {
        return order.setOrderItemList(
            order.getOrderItemList()
                .stream()
                .map(item -> {
                    Product p = productRepository.findById(item.getId()).orElseThrow();
                    return item.setProduct(new ProductDto().setId(p.getId())
                        .setName(p.getName())
                        .setPrice(p.getPrice())
                        .setSellerId(p.getSellerId()));
                }).toList());
    }
}
