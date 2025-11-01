package com.kafka.benchmark;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import com.google.gson.Gson;

/**
 * Kafka Producer Performance Testing Suite
 * 
 * This class orchestrates multi-threaded producer performance tests across
 * multiple Kafka topics and collects comprehensive metrics including throughput,
 * latency percentiles, and timing data.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Kafka Producer Performance Tests...");
        
        try {
            Properties prop = new Properties();
            InputStream input = null;
            input = new FileInputStream("config/config.properties");
            prop.load(input);
            
            int topicStart = Integer.parseInt(prop.getProperty("topicstart"));
            int topicEnd = Integer.parseInt(prop.getProperty("topicend"));
            
            System.out.println("**********************************************************");
            System.out.println("Topics start from " + topicStart + " to " + topicEnd + " with " + System.lineSeparator() + 
                    " numOfRecords " + prop.getProperty("numOfRecords") + System.lineSeparator() +
                    " recordSize " + prop.getProperty("recordSize") + System.lineSeparator() +
                    " throughputRate per sec " + prop.getProperty("throughputRate") + System.lineSeparator() +
                    " Result Index is " + prop.getProperty("indexname"));
            System.out.println("**********************************************************");
            
            for(int i = topicStart; i < topicEnd; i++) {
                ExecutorThread ET = new ExecutorThread(i);
                ET.start();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

/**
 * Thread class for executing producer performance tests on individual topics
 */
class ExecutorThread extends Thread {

    int topic;
    
    protected ExecutorThread(int topicNo){
        topic = topicNo;
    }
    
    public void run() {
        try {
            performAction(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void performAction(int topicN){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Properties prop = new Properties();
            InputStream input = null;
            input = new FileInputStream("config/config.properties");
            prop.load(input);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String startTime = sdf.format(new Date());
            System.out.println("Start time ---> " + startTime + " topic " + prop.getProperty("topicPrefix") + topicN);

            String command = prop.getProperty("kafkaHome") + "/bin/kafka-producer-perf-test --producer-props bootstrap.servers=" + 
                    prop.getProperty("bootstrapServers") + 
                    " --topic " + prop.getProperty("topicPrefix") + topicN +
                    " --num-records " + prop.getProperty("numOfRecords") +
                    " --record-size " + prop.getProperty("recordSize") +
                    " --throughput " + prop.getProperty("throughputRate") +
                    " --producer.config " + prop.getProperty("producerConfig");

            processBuilder.command("bash", "-c", command);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
           
            String line;
            while ((line = reader.readLine()) != null) {
                if(line != null && line.contains("99.9th.")) {
                    break;
                }   
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                String endTime = sdf.format(new Date());
                pushToIndex(line, startTime, endTime, prop.getProperty("indexname"));
            } else {
                System.out.println("Something unexpected happened!!!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    void pushToIndex(String line, String startTime, String endTime, String index) {
        try {
            if(line != null && line.length() > 0) {
                String fArray[] = line.split("\\,");
                
                HashMap<Object, Object> hm = new HashMap<Object, Object>();
                
                hm.put("start time", startTime);
                hm.put("End time", endTime);
                                                        
                hm.put("records sent", Double.parseDouble(fArray[0].substring(0, fArray[0].indexOf("records sent")).trim()));
                hm.put("records/sec", Double.parseDouble(fArray[1].substring(0, fArray[1].indexOf("records/sec")).trim()));
                hm.put("MB/sec", Double.parseDouble(fArray[1].substring(fArray[1].indexOf("(")+1, fArray[1].indexOf("MB/sec")).trim()));
                hm.put("avg latency ms", Double.parseDouble(fArray[2].substring(0, fArray[2].indexOf("ms avg latency")).trim()));
                hm.put("max latency ms", Double.parseDouble(fArray[3].substring(0, fArray[3].indexOf("ms max latency")).trim()));
                
                hm.put("50th percentile ms", Double.parseDouble(fArray[4].substring(0, fArray[4].indexOf("ms 50th")).trim()));
                hm.put("95th percentile ms", Double.parseDouble(fArray[5].substring(0, fArray[5].indexOf("ms 95th")).trim()));
                hm.put("99th percentile ms", Double.parseDouble(fArray[6].substring(0, fArray[6].indexOf("ms 99th")).trim()));
                hm.put("99.9th percentile ms", Double.parseDouble(fArray[7].substring(0, fArray[7].indexOf("ms 99.9th.")).trim()));
                
                Gson gson = new Gson();
                String jsonString = gson.toJson(hm);
                System.out.println(jsonString);
                
                Properties prop = new Properties();
                InputStream input = new FileInputStream("config/config.properties");
                prop.load(input);
                
                RestClient restclient = RestClient.builder(
                    new HttpHost(prop.getProperty("elasticsearchHost"), 
                               Integer.parseInt(prop.getProperty("elasticsearchPort")))).build();
                HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
                
                Response indexResponse = restclient.performRequest("POST", "/" + index + "/oasis", 
                    Collections.<String, String>emptyMap(), entity);
                System.out.println(EntityUtils.toString(indexResponse.getEntity()));
                restclient.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}