package org.stand.springbootecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.stand.springbootecommerce.entity.user.Product;
import org.stand.springbootecommerce.entity.user.ProductCategory;
import org.stand.springbootecommerce.repository.ProductCategoryRepository;
import org.stand.springbootecommerce.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DataBaseInitializer implements CommandLineRunner {
        private static final String CATEGORY_DESCRIPTION = "Category description";
        private final ProductRepository productRepository;
        private final ProductCategoryRepository productCategoryRepository;

        @Override
        public void run(String... args) throws Exception {
                // GPT3.5 generated data

                ProductCategory categorySmartTv = ProductCategory.builder().name("SmartTv")
                                .description(CATEGORY_DESCRIPTION).build();
                ProductCategory categoryLaptop = ProductCategory.builder().name("Laptop")
                                .description(CATEGORY_DESCRIPTION).build();
                ProductCategory categorySmartphone = ProductCategory.builder().name("Smartphone")
                                .description("Category for smartphones").build();
                ProductCategory categoryConsole = ProductCategory.builder().name("Console")
                                .description(CATEGORY_DESCRIPTION).build();
                ProductCategory categoryCamera = ProductCategory.builder().name("Camera")
                                .description(CATEGORY_DESCRIPTION).build();
                ProductCategory categoryHeadphone = ProductCategory.builder().name("Headphone")
                                .description(CATEGORY_DESCRIPTION).build();
                ProductCategory categorySmallAppliance = ProductCategory.builder().name("Small Appliance")
                                .description(CATEGORY_DESCRIPTION).build();

                productCategoryRepository.saveAll(Objects.requireNonNull(
                                List.of(categorySmartphone, categoryLaptop, categorySmartTv, categoryConsole,
                                                categoryCamera, categoryHeadphone, categorySmallAppliance)));

                productRepository.saveAll(
                                Objects.requireNonNull(List.of(
                                                Product.builder()
                                                                .name("APPLE iPhone 14 256GB Mezzanotte")
                                                                .description("Con iOS 16 puoi divertirti a personalizzare la schermata di blocco in modi tutti nuovi. Porta in primo piano il soggetto di una foto, lasciando data, ora e altre info sullo sfondo.")
                                                                .shortDescription(
                                                                                "Apple iPhone 14, IOS 16, Designed in California.")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_105475633?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(1099.99))
                                                                .quantity(5)
                                                                .category(categorySmartphone)
                                                                .build(),

                                                Product.builder()
                                                                .name("Samsung Galaxy S22 Ultra")
                                                                .description("Experience the ultimate smartphone with the Samsung Galaxy S22 Ultra. It features a stunning display, powerful camera, and more.")
                                                                .shortDescription("Samsung Galaxy S22 Ultra")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_96461586?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(1299.99))
                                                                .quantity(10)
                                                                .category(categorySmartphone)
                                                                .build(),

                                                Product.builder()
                                                                .name("Sony PlayStation 5")
                                                                .description("Get ready for the next level of gaming with the Sony PlayStation 5. It offers incredible graphics and lightning-fast load times.")
                                                                .shortDescription("Sony PS5")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_98368430?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(499.99))
                                                                .quantity(15)
                                                                .category(categoryConsole)
                                                                .build(),

                                                Product.builder()
                                                                .name("Apple MacBook Pro 16-inch")
                                                                .description("Experience unmatched performance with the Apple MacBook Pro 16-inch. It's perfect for creative professionals.")
                                                                .shortDescription("MacBook Pro 16-inch")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_107112285?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(2399.99))
                                                                .quantity(8)
                                                                .category(categoryLaptop)
                                                                .build(),

                                                Product.builder()
                                                                .name("Canon EOS 5D Mark IV")
                                                                .description("Capture stunning photographs and videos with the Canon EOS 5D Mark IV DSLR camera.")
                                                                .shortDescription("Canon EOS 5D Mark IV")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_84923772?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(2499.99))
                                                                .quantity(0)
                                                                .category(categoryCamera)
                                                                .build(),

                                                Product.builder()
                                                                .name("Bose QuietComfort 45 Headphones")
                                                                .description("Enjoy immersive sound and noise-canceling technology with the Bose QuietComfort 45 headphones.")
                                                                .shortDescription("Bose QC 45")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_120524025?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(349.99))
                                                                .quantity(0)
                                                                .category(categoryHeadphone)
                                                                .build(),

                                                Product.builder()
                                                                .name("Nintendo Switch OLED")
                                                                .description("Experience gaming on the go with the Nintendo Switch OLED, featuring a vibrant OLED screen.")
                                                                .shortDescription("Nintendo Switch OLED")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_88253924?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(299.99))
                                                                .quantity(0)
                                                                .category(categoryConsole)
                                                                .build(),

                                                Product.builder()
                                                                .name("Dell XPS 13 Laptop")
                                                                .description("Experience premium performance with the Dell XPS 13 laptop, featuring a stunning InfinityEdge display.")
                                                                .shortDescription("Dell XPS 13")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_107598868?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(1499.99))
                                                                .quantity(10)
                                                                .category(categoryLaptop)
                                                                .build(),

                                                Product.builder()
                                                                .name("Sony 65-inch 4K OLED TV")
                                                                .description("Immerse yourself in cinematic visuals with the Sony 65-inch 4K OLED TV.")
                                                                .shortDescription("Sony 4K OLED TV")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_108257733?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(1999.99))
                                                                .quantity(5)
                                                                .category(categorySmartTv)
                                                                .build(),

                                                Product.builder()
                                                                .name("Google Nest Hub Max")
                                                                .description("Stay connected and control your smart home with the Google Nest Hub Max.")
                                                                .shortDescription("Google Nest Hub Max")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_84910923?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(229.99))
                                                                .quantity(15)
                                                                .category(categoryConsole)
                                                                .build(),

                                                Product.builder()
                                                                .name("Samsung 75-inch QLED 8K TV")
                                                                .description("Experience breathtaking visuals with the Samsung 75-inch QLED 8K TV, featuring Quantum Dot technology.")
                                                                .shortDescription("Samsung QLED 8K TV")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_106515355?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(3499.99))
                                                                .quantity(8)
                                                                .category(categorySmartTv)
                                                                .build(),

                                                Product.builder()
                                                                .name("Apple Watch Series 7")
                                                                .description("Stay connected and track your health with the Apple Watch Series 7.")
                                                                .shortDescription("Apple Watch Series 7")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_97264842?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(399.99))
                                                                .quantity(20)
                                                                .category(categorySmartphone)
                                                                .build(),

                                                Product.builder()
                                                                .name("Microsoft Surface Pro 8")
                                                                .description("Enhance your productivity with the Microsoft Surface Pro 8, a versatile 2-in-1 laptop.")
                                                                .shortDescription("Surface Pro 8")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_98750346?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(1299.99))
                                                                .quantity(12)
                                                                .category(categoryLaptop)
                                                                .build(),

                                                Product.builder()
                                                                .name("Sony WH-1000XM4 Noise-Canceling Headphones")
                                                                .description("Enjoy high-quality audio and noise cancellation with Sony WH-1000XM4 headphones.")
                                                                .shortDescription("Sony WH-1000XM4")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_107497361?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(299.99))
                                                                .quantity(15)
                                                                .category(categoryHeadphone)
                                                                .build(),

                                                Product.builder()
                                                                .name("Sony PlayStation 5 Pro")
                                                                .description("The Sony PlayStation 5 Pro offers even more power and performance for gaming enthusiasts.")
                                                                .shortDescription("PS5 Pro")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_101131565?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(599.99))
                                                                .quantity(10)
                                                                .category(categoryConsole)
                                                                .build(),

                                                Product.builder()
                                                                .name("Canon EOS R5 Mirrorless Camera")
                                                                .description("Capture high-resolution photos and 8K video with the Canon EOS R5 mirrorless camera.")
                                                                .shortDescription("Canon EOS R5")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_84646891?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(3499.99))
                                                                .quantity(5)
                                                                .category(categoryCamera)
                                                                .build(),

                                                Product.builder()
                                                                .name("Dyson V11 Cordless Vacuum Cleaner")
                                                                .description("Keep your home clean with the Dyson V11 cordless vacuum cleaner.")
                                                                .shortDescription("Dyson V11")
                                                                .image("https://assets.mmsrg.com/isr/166325/c1/-/ASSET_MMS_107115823?x=960&y=720&format=jpg&quality=80&sp=yes&strip=yes&trim&ex=960&ey=720&align=center&resizesource&unsharp=1.5x1+0.7+0.02&cox=0&coy=0&cdx=960&cdy=720")
                                                                .price(BigDecimal.valueOf(499.99))
                                                                .quantity(15)
                                                                .category(categorySmallAppliance)
                                                                .build())));
        }
}