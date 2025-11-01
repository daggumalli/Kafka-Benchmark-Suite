package com.kafka.benchmark;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;
import java.text.DateFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import com.google.gson.Gson;

/**
 * Kafka Consumer Performance Testing Suite
 * 
 * This class orchestrates multi-threaded consumer performance tests across
 * multiple Kafka topics and collects comprehensive metrics including consumption
 * rates, fetch times, and rebalance metrics.
 */
public class ConsumerJ {

    public static void main(String[] args) {
        System.out.println("Starting Kafka Consumer Performance Tests...");
        
        try {
            Properties prop = new Properties();
            InputStream input = null;
            input = new FileInputStream("config/consumer.properties");
            prop.load(input);
            
            int topicStart = Integer.parseInt(prop.getProperty("topicstart"));
            int topicEnd = Integer.parseInt(prop.getProperty("topicend"));
            
            System.out.println("**********************************************************");
            System.out.println("Topics start from " + topicStart + " to " + topicEnd + " with " + System.lineSeparator() + 
                    " numOfRecords " + prop.getProperty("numOfRecords") + System.lineSeparator() +
                    " groupid " + prop.getProperty("groupid") + System.lineSeparator() +
                    " latest? " + prop.getProperty("latest") + System.lineSeparator() +
                    " processingthreads " + prop.getProperty("processingthreads") + System.lineSeparator() +
                    " fetchThreads " + prop.getProperty("fetchThreads") + System.lineSeparator() +
                    " timeout " + prop.getProperty("timeout") + System.lineSeparator() +
                    " reportingIntervalms " + prop.getProperty("reportingIntervalms") + System.lineSeparator() +
                    " Result Index is " + prop.getProperty("indexname"));
            System.out.println("**********************************************************");
            
            for(int i = topicStart; i < topicEnd; i++) {
                ConsConsExecutorThread ET = new ConsConsExecutorThread(i);
                ET.start();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

/**
 * Thread class for executing consumer performance tests on individual topics
 */
class ConsConsExecutorThread extends Thread {

    int topic;
    
    protected ConsConsExecutorThread(int topicNo){
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
            input = new FileInputStream("config/consumer.properties");
            prop.load(input);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
            String startTime = sdf.format(new Date());
            System.out.println("Start time ---> " + startTime + " topic " + prop.getProperty("topicPrefix") + topicN);

            String command = null;
            if(prop.getProperty("latest").equalsIgnoreCase("no")) {
                command = prop.getProperty("kafkaHome") + "/bin/kafka-consumer-perf-test --broker-list=" + 
                        prop.getProperty("bootstrapServers") +
                        " --topic " + prop.getProperty("topicPrefix") + topicN +
                        " --messages " + prop.getProperty("numOfRecords") +
                        " --timeout " + prop.getProperty("timeout") +
                        " --consumer.config " + prop.getProperty("consumerConfig");
            } else {
                command = prop.getProperty("kafkaHome") + "/bin/kafka-consumer-perf-test --broker-list=" + 
                        prop.getProperty("bootstrapServers") +
                        " --topic " + prop.getProperty("topicPrefix") + topicN +
                        " --messages " + prop.getProperty("numOfRecords") +
                        " --timeout " + prop.getProperty("timeout") +
                        " --consumer.config " + prop.getProperty("consumerConfig") +
                        " --from-latest";            	
            }

            System.out.println("command is ---> " + command);                
            processBuilder.command("bash", "-c", command);

            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
           
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(":") && line.contains("-") && !line.contains("WARNING:")) {
                    break;
                }   
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                pushToIndex(line, prop.getProperty("indexname"), topicN);
                System.out.println(output);
            } else {
                System.out.println("Something unexpected happened!!!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    void pushToIndex(String line, String index, int tn) {
        try {
            if(line != null && line.length() > 0) {
                String fArray[] = line.split("\\,");
                
                HashMap<Object, Object> hm = new HashMap<Object, Object>();
                
                DateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");              
                DateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                toFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));

                hm.put("topic", tn);
                hm.put("start.time", toFormat.format(fromFormat.parse(fArray[0])));
                hm.put("end.time", toFormat.format(fromFormat.parse(fArray[1])));
                hm.put("data.consumed.in.MB", Double.parseDouble(fArray[2].trim()));
                hm.put("MB.sec", Double.parseDouble(fArray[3].trim()));
                hm.put("data.consumed.in.nMsg", Double.parseDouble(fArray[4].trim()));
                hm.put("nMsg.sec", Double.parseDouble(fArray[5].trim()));
                hm.put("rebalance.time.ms", Double.parseDouble(fArray[6].trim()));
                hm.put("fetch.time.ms", Double.parseDouble(fArray[7].trim()));
                hm.put("fetch.MB.sec", Double.parseDouble(fArray[8].trim()));
                hm.put("fetch.nMsg.sec", Double.parseDouble(fArray[9].trim()));
                
                Gson gson = new Gson();
                String jsonString = gson.toJson(hm);
                System.out.println(jsonString);
                
                Properties prop = new Properties();
                InputStream input = new FileInputStream("config/consumer.properties");
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