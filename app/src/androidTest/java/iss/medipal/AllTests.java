package iss.medipal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import iss.medipal.model.Appointment;
import iss.medipal.model.Category;
import iss.medipal.model.Consumption;
import iss.medipal.model.HealthBio;

/**
 * Created by Thirumal on 26/3/2017.
 */

public class AllTests extends TestCase {

    public static Test suite()
    {
        TestSuite suite=new TestSuite("Test for Package "+MedipalUnitTest.context.getPackageName());
        suite.addTestSuite(MedicineUnitTest.class);
        suite.addTestSuite(AppointmentUnitTest.class);
        suite.addTestSuite(MeasurementUnitTest.class);
        suite.addTestSuite(HealthBioUnitTest.class);
        suite.addTestSuite(ICEUnitTest.class);
        suite.addTestSuite(PersonBioUnitTest.class);
        suite.addTestSuite(ConsumptionUnitTest.class);
        suite.addTestSuite(ReminderUnitTest.class);
        suite.addTestSuite(CategoryUnitTest.class);
        return suite;
    }
}
