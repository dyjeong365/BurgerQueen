package app.product;

import app.product.subproduct.BurgerSet;
import app.product.subproduct.Drink;
import app.product.subproduct.Hamburger;
import app.product.subproduct.Side;

import java.util.Scanner;

public class Cart {
    private Scanner scanner = new Scanner(System.in);
    private Product[] items = new Product[0];
    private ProductRepository productRepository;
    private Menu menu;

    public Cart(ProductRepository productRepository, Menu menu) {
        this.productRepository = productRepository;
        this.menu = menu;
    }

    private void chooseOption(Product product) {

        String input;

        if (product instanceof Hamburger) {
            System.out.printf("단품으로 주문하시겠어요? (1)_단품(%d원) (2)_세트(%d원)\n",
                    product.getPrice(), ((Hamburger) product).getBurgerSetPrice());
            input = scanner.nextLine();
            if (input.equals("2")) ((Hamburger) product).setIsBurgerSet(true);
        } else if (product instanceof Side) {
            System.out.printf("케첩은 몇개가 필요하신가요?\n");
            input = scanner.nextLine();
            ((Side) product).setKetchup(Integer.parseInt(input));
        } else if (product instanceof Drink) {
            System.out.printf("빨대가 필요하신가요? (1)_예 (2)_아니오\n");
            input = scanner.nextLine();
            if (input.equals("2")) ((Drink) product).setHasStraw(false);
        }
    }

    private BurgerSet composeSet(Hamburger hamburger) {
        System.out.println("사이드를 골라주세요");
        menu.printSides(false);

        String sideId = scanner.nextLine();
        Side side = (Side) productRepository.findById(Integer.parseInt(sideId));
        Side newSide = new Side(side);
        chooseOption(newSide);

        System.out.println("음료를 골라주세요.");
        menu.printDrinks(false);

        String drinkId = scanner.nextLine();
        Drink drink = (Drink) productRepository.findById(Integer.parseInt(drinkId));
        Drink newDrink = new Drink(drink);
        chooseOption(newDrink);

        String name = hamburger.getName();
        int price = hamburger.getBurgerSetPrice();
        int kcal = hamburger.getKcal() + side.getKcal() + drink.getKcal();

        return new BurgerSet(name, price, kcal, hamburger, newSide, newDrink);
    }

    public void printCart() {
        System.out.println("🧺 장바구니");
        System.out.println("-".repeat(60));
        printCartItemDetails();
        System.out.println("-".repeat(60));
        System.out.printf("합계 : %d원\n", calculateTotalPrice());

        System.out.println("이전으로 돌아가려면 엔터를 누르세요. ");
        scanner.nextLine();

    }

    public void addToCart(int productId) {
        Product product = productRepository.findById(productId);

        Product newProduct;
        if (product instanceof Hamburger) newProduct = new Hamburger((Hamburger) product);
        else if (product instanceof Side) newProduct = new Side((Side) product);
        else newProduct = new Drink((Drink) product);

        chooseOption(newProduct);

        if (newProduct instanceof Hamburger) {
            Hamburger hamburger = (Hamburger) newProduct;
            if (hamburger.isBurgerSet()) newProduct = composeSet(hamburger);
        }

        Product[] newItems = new Product[items.length + 1];
        System.arraycopy(items, 0, newItems, 0, items.length);
        newItems[newItems.length - 1] = newProduct;
        items = newItems;

        System.out.printf("[📣] %s를(을) 장바구니에 담았습니다.\n", product.getName());
    }

    protected int calculateTotalPrice() {
        int totalPrice = 0;

        for (Product product : items) {
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }

    protected void printCartItemDetails() {
        for (Product product : items) {
            if (product instanceof BurgerSet) {
                BurgerSet burgerSet = (BurgerSet) product;
                System.out.printf("  %s %6d원 (%s(케첩 %d개), %s(빨대 %s))\n", burgerSet.getName(), burgerSet.getPrice(),
                        burgerSet.getSide().getName(), burgerSet.getSide().getKetchup(), burgerSet.getDrink().getName()
                        , burgerSet.getDrink().HasStraw() ? "있음" : "없음");
            } else if (product instanceof Hamburger) {
                Hamburger hamburger = (Hamburger) product;
                System.out.printf("  %-8s %6d원 (단품)\n", hamburger.getName(), hamburger.getPrice());
            } else if (product instanceof Side) {
                Side side = (Side) product;
                System.out.printf("  %-8s %6d원 (케첩 %d개)\n", side.getName(), side.getPrice(),
                        ((Side) product).getKetchup());
            } else if (product instanceof Drink) {
                Drink drink = (Drink) product;
                System.out.printf("  %-8s %6d원 (빨대 %s)\n", drink.getName(), drink.getPrice(),
                        ((Drink) product).HasStraw() ? "있음" : "없음");
            }
        }
    }
}
