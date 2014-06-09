package quickdt.csvReader;
import com.google.common.collect.Sets;
import org.jooq.tools.csv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Load training data in from redshift.
 */
public class CSVToMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToMap.class);

    private String bucketName;
    //   private S3FileLoader s3FileLoader;


    public static final HashSet<String> GOOD_HEADERS = Sets.newHashSet("an_user_id", "an_url",
            "an_domain", "an_seller_id", "an_publisher_id", "an_site_id", "an_os", "an_browser", "an_country",
            "an_region", "an_city", "an_postal_code", "an_dma", "an_placement_position", "an_reserve", "an_eap", "an_ecp",
            "os_account_id", "os_campaign_id", "os_creative_id", "created_at", "an_no_cookies", "an_secure_inventory", "an_ip_address",
            "is_click");
    public static final String[] HEADERS = new String[]{"an_auction_id", "an_user_id", "an_url", "an_url_domain",
            "an_domain", "an_seller_id", "an_publisher_id", "an_site_id", "an_os", "an_browser", "an_country",
            "an_region", "an_city", "an_postal_code", "an_dma", "an_placement_position", "an_placement_size",
            "an_first_touch_type", "an_first_touch_os_campaign_id", "an_first_touch_os_creative_id",
            "os_bd_bid_nobid_outcome", "an_reserve", "an_eap", "an_ecp", "os_bidder_name", "os_bd_our_price_cpm",
            "os_bd_customer_price_cpm", "os_bd_raw_click_score", "os_bd_calibrated_click_score",
            "os_bd_click_probability", "os_bd_target_cpc", "os_account_id", "os_campaign_id", "os_creative_id",
            "created_at", "an_no_cookies", "an_secure_inventory", "an_ip_address", "an_bday_cookie_first_set",
            "os_bd_bidding_strategy", "os_bd_bidder_version", "os_bd_campaign_eligibility_reason",
            "os_bd_click_probability_adjusted", "an_our_cost_cpm", "an_customer_cost_cpm", "is_click", "randomsample"};


    /**
     * Load the raw training data.
     *
     * @return a list of maps
     */
    public static List<Map<String, Serializable>> loadRows(String inputFile) {
        List<Map<String, Serializable>> rawData = new ArrayList<>();
        List<String[]> rowData = new LinkedList<>();
        try (CSVReader csvReader = new CSVReader(createReader(inputFile), '|')) {
            while (csvReader.hasNext()) {
                rowData.add(csvReader.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        for(String[] row : rowData) {
            Map<String, Serializable> rowMap = convertRowToMap(row);
            if (rowMap != null) {
                rawData.add(rowMap);
            }
        }
        return rawData;
    }

    private static InputStreamReader createReader(final String inputFile) {
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader =  new InputStreamReader(new FileInputStream(inputFile));
            return inputStreamReader;
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    private static Map<String, Serializable> convertRowToMap(final String[] rowData) {
        return isValid(rowData) ? convertToMap(rowData) : null;
    }

    private static Map<String, Serializable> convertToMap(final String[] rowData) {
        HashMap<String, Serializable> row = new HashMap<>();
        if (rowData != null) {
            for (int i = 0; i < rowData.length; i++) {
                if (GOOD_HEADERS.contains(HEADERS[i]))
                    row.put(HEADERS[i], rowData[i]);
            }
        }
        return row;
    }

    private static boolean isValid(final String[] rowArray) {
        if (rowArray == null)
            return false;

        if (rowArray.length != 47) {
            LOGGER.warn("Number of elements in redshift row is not valid - {}", rowArray.length);
            return false;
        }

        return true;
    }

}


