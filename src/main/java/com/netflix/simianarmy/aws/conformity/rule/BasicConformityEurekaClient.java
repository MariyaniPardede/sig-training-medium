package com.netflix.simianarmy.aws.conformity.rule;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;


public class BasicConformityEurekaClient implements ConformityEurekaClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicConformityEurekaClient.class);

    private final DiscoveryClient discoveryClient;

    /**
     * Constructor.
     * @param discoveryClient the client to access Discovery/Eureka service.
     */
    public BasicConformityEurekaClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public boolean hasHealthCheckUrl(String region, String instanceId) {
        List<InstanceInfo> instanceInfos = discoveryClient.getInstancesById(instanceId);
        for (InstanceInfo info : instanceInfos) {
            Set<String> healthCheckUrls = info.getHealthCheckUrls();
            if (healthCheckUrls != null && !healthCheckUrls.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasStatusUrl(String region, String instanceId) {
        List<InstanceInfo> instanceInfos = discoveryClient.getInstancesById(instanceId);
        for (InstanceInfo info : instanceInfos) {
            String statusPageUrl = info.getStatusPageUrl();
            if (!StringUtils.isEmpty(statusPageUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isHealthy(String region, String instanceId) {
        List<InstanceInfo> instanceInfos = discoveryClient.getInstancesById(instanceId);
        if (instanceInfos.isEmpty()) {           
            return false;
        } else {
            for (InstanceInfo info : instanceInfos) {
                InstanceInfo.InstanceStatus status = info.getStatus();
                if (!status.equals(InstanceInfo.InstanceStatus.UP)
                        && !status.equals(InstanceInfo.InstanceStatus.STARTING)) {
                    return false;
                }
            }
        }
        return true;
    }
}
