

package com.netflix.simianarmy.aws.janitor.rule.volume;

import com.netflix.simianarmy.MonkeyCalendar;
import com.netflix.simianarmy.Resource;
import com.netflix.simianarmy.aws.AWSResource;
import com.netflix.simianarmy.aws.janitor.crawler.edda.EddaEBSVolumeJanitorCrawler;
import com.netflix.simianarmy.janitor.JanitorMonkey;
import com.netflix.simianarmy.janitor.Rule;
import org.apache.commons.lang.Validate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


public class DeleteOnTerminationRule implements Rule {

    

    private final MonkeyCalendar calendar;

    private final int retentionDays;

   
    public DeleteOnTerminationRule(MonkeyCalendar calendar, int retentionDays) {
        Validate.notNull(calendar);
        Validate.isTrue(retentionDays >= 0);
        this.calendar = calendar;
        this.retentionDays = retentionDays;
    }

    @Override
    public boolean isValid(Resource resource) {
        Validate.notNull(resource);
        if (!resource.getResourceType().name().equals("EBS_VOLUME")) {
            return true;
        }

       
        if (!"available".equals(((AWSResource) resource).getAWSResourceState())) {
            return true;
        }
        String janitorTag = resource.getTag(JanitorMonkey.JANITOR_TAG);
        if (janitorTag != null) {
            if ("donotmark".equals(janitorTag)) {
               
                return true;
            }
            try {
                
                Date userSpecifiedDate = new Date(
                        TERMINATION_DATE_FORMATTER.parseDateTime(janitorTag).getMillis());
                resource.setExpectedTerminationTime(userSpecifiedDate);
                resource.setTerminationReason(String.format("User specified termination date %s", janitorTag));
                return false;
            } catch (Exception e) {
               
            }
        }

        if ("true".equals(resource.getAdditionalField(EddaEBSVolumeJanitorCrawler.DELETE_ON_TERMINATION))) {
            if (resource.getExpectedTerminationTime() == null) {
                Date terminationTime = calendar.getBusinessDay(calendar.now().getTime(), retentionDays);
                resource.setExpectedTerminationTime(terminationTime);
                resource.setTerminationReason(TERMINATION_REASON);
                
            } 
            return false;
        }
        return true;
    }
}

