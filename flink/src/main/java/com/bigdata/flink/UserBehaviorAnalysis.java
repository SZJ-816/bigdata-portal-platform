package com.bigdata.flink;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Properties;

public class UserBehaviorAnalysis {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60000);

        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", "kafka:9092");
        kafkaProps.setProperty("group.id", "flink-behavior-analysis");

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
            "user-behavior",
            new SimpleStringSchema(),
            kafkaProps
        );
        consumer.setStartFromLatest();

        DataStream<String> stream = env.addSource(consumer);

        DataStream<String> pvStats = stream
            .map(message -> {
                JSONObject obj = JSON.parseObject(message);
                obj.put("event_time", System.currentTimeMillis());
                return obj.getString("event_type");
            })
            .filter(type -> type != null)
            .keyBy(type -> type)
            .timeWindow(Time.minutes(1))
            .aggregate(new CountAggregate())
            .map(result -> JSON.toJSONString(result));

        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", "kafka:9092");

        FlinkKafkaProducer<String> producer = new FlinkKafkaProducer<>(
            "analytics-result",
            new SimpleStringSchema(),
            producerProps
        );

        pvStats.addSink(producer);

        env.execute("User Behavior Realtime Analysis");
    }

    public static class CountAggregate implements AggregateFunction<String, Long, JSONObject> {
        @Override
        public Long createAccumulator() { return 0L; }

        @Override
        public Long add(String value, Long accumulator) { return accumulator + 1; }

        @Override
        public JSONObject getResult(Long accumulator) {
            JSONObject result = new JSONObject();
            result.put("count", accumulator);
            result.put("timestamp", System.currentTimeMillis());
            return result;
        }

        @Override
        public Long merge(Long a, Long b) { return a + b; }
    }
}
