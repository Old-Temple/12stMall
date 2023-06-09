package stmall.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import stmall.DeliveryApplication;
import stmall.domain.DeliveryCancelled;
import stmall.domain.DeliveryStarted;

@Entity
@Table(name = "Delivery_table")
@Data
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;

    private Long orderId;

    private String productName;

    private Long productId;

    private Integer qty;

    private String status;

    private String courier;

    @PostPersist
    public void onPostPersist() {
        // 어그리게이트 후킹해서 실행되는 애라 따로 commit 해준다면 필요없음
        // DeliveryStarted deliveryStarted = new DeliveryStarted(this);
        // deliveryStarted.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        // 얘도 후킹되어서 실행되는 애라 두번 찍힐 수 있음. 따로 commit 해준다면 막아놓자
        // DeliveryCancelled deliveryCancelled = new DeliveryCancelled(this);
        // deliveryCancelled.publishAfterCommit();
    }

    public static DeliveryRepository repository() {
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(
            DeliveryRepository.class
        );
        return deliveryRepository;
    }

    /**
     * Command 구현체
     * @param completeDeliveryCommand
     */
    public void completeDelivery(CompleteDeliveryCommand completeDeliveryCommand) {
        this.setCourier(completeDeliveryCommand.getCourier());
        this.setStatus("DeliveryCompleted");
        DeliveryCompleted deliveryCompleted = new DeliveryCompleted(this);
        deliveryCompleted.publishAfterCommit();
    }

    public void returnDelivery(ReturnDeliveryCommand returnDeliveryCommand) {
        this.setCourier(returnDeliveryCommand.getCourier());
        this.setStatus("DeliveryReturned");
        DeliveryReturned deliveryReturned = new DeliveryReturned(this);
        deliveryReturned.publishAfterCommit();
    }

    public static void startDelivery(OrderPlaced orderPlaced) {
        // /** Example 1:  new item 
        Delivery delivery = new Delivery();
        
        //setter
        delivery.setOrderId(orderPlaced.getId());
        delivery.setProductId(orderPlaced.getProductId());
        delivery.setProductName(orderPlaced.getProductName());
        delivery.setQty(orderPlaced.getQty());
        delivery.setUserId(orderPlaced.getUserId());
        delivery.setStatus("DeliveryStarted");
        repository().save(delivery);    //repository insert

        DeliveryStarted deliveryStarted = new DeliveryStarted(delivery);
        deliveryStarted.publishAfterCommit();
        // */

        /** Example 2:  finding and process
        
        repository().findById(orderPlaced.get???()).ifPresent(delivery->{
            
            delivery // do something
            repository().save(delivery);

            DeliveryStarted deliveryStarted = new DeliveryStarted(delivery);
            deliveryStarted.publishAfterCommit();

         });
        */

    }

    public static void cancleDelivery(OrderCancelled orderCancelled) {
        /** Example 1:  new item 
        Delivery delivery = new Delivery();
        repository().save(delivery);

        DeliveryCancelled deliveryCancelled = new DeliveryCancelled(delivery);
        deliveryCancelled.publishAfterCommit();
        */

        // /** Example 2:  finding and process
        repository().findByOrderId(orderCancelled.getId()).ifPresent(delivery->{
            
            delivery.setStatus("DeliveryCancelled"); // do something
            repository().save(delivery);

            DeliveryCancelled deliveryCancelled = new DeliveryCancelled(delivery);
            deliveryCancelled.publishAfterCommit();

         });
        // */

    }
}
