package com.bolocloud.eurekaclientcall.controller;

import com.bolocloud.eurekaclientcall.service.FeignService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.loadbalancer.ZoneAwareLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RestController
public class RestTemController {

    private  static Logger log = LoggerFactory.getLogger(RestTemController.class);

    @Resource(name = "restTemplateCommon")
    RestTemplate template;

    @Autowired
    private LoadBalancerClient client;

    @Autowired
    private SpringClientFactory factory;

    @Autowired
    private FeignService feignClient;

    @Value("${myname}")
    private String myname;

    @Value("${myconfig.remote.invoker}")
    private String invoker;


    @GetMapping(value = "/call")
//    @HystrixCommand
    public String call(){
        String msg = template.getForObject("http://consul-provider/provider",String.class);
        return msg;
    }

    @GetMapping(value = "/clientcall")
    public ServiceInstance clientcall(){
        ServiceInstance si = client.choose("consul-provider");
        return si;
    }

    @GetMapping(value = "/factory")
    public String factory() {
        ZoneAwareLoadBalancer lb = (ZoneAwareLoadBalancer) factory.getLoadBalancer("consul-call");
        String msg = lb.getRule().getClass().getName();

        ZoneAwareLoadBalancer lb2 = (ZoneAwareLoadBalancer) factory.getLoadBalancer("default");
        msg += "====="+lb2.getRule().getClass().getName();
        return msg;
    }

    @GetMapping("/feigncall")
    public String feigncall(){
        String msg = feignClient.getHello();
        return msg +":"+ myname + "-"+invoker;
//        log.info("=====<invoker log>=====");
////        return msg;
    }


}
