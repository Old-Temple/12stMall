package stmall.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import stmall.config.kafka.KafkaProcessor;
import stmall.domain.*;

@Service
@Transactional
public class PolicyHandler {

    @Autowired
    DeliveryRepository deliveryRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='OrderPlaced'")
    public void wheneverOrderPlaced_StartDelivery(@Payload OrderPlaced orderPlaced) {
        OrderPlaced event = orderPlaced;
        Delivery.startDelivery(event);
    }

    @StreamListener(value = KafkaProcessor.INPUT, condition = "headers['type']=='OrderCancelled'")
    public void wheneverOrderCancelled_CancleDelivery(@Payload OrderCancelled orderCancelled) {
        OrderCancelled event = orderCancelled;
        Delivery.cancleDelivery(event);
    }
}
