package iss.medipal;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import iss.medipal.dao.AppointmentDao;
import iss.medipal.dao.impl.AppointmentDaoImpl;
import iss.medipal.model.Appointment;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by sreekumar on 3/26/2017.
 */

public class AppointmentUnitTest extends TestCase implements MedipalUnitTest {

    Appointment appointment;
    AppointmentDao appointmentDao;

    @Before
    public void setUp() {
        appointment = populateAppointment();
        appointmentDao = new AppointmentDaoImpl(context);

    }

    @After
    public void tearDown(){
        appointmentDao.clearTable();
        appointmentDao=null;
        appointment=null;

    }
    @Test
    public void testAddAppointment() throws Exception {
        assertNotEquals("Appointment added",-1,appointmentDao.addAppointment(appointment));
    }

    @Test
    public void testUpdateAppointment(){
        appointmentDao.addAppointment(appointment);
        appointment.setLocation("Kent ridge bus terminal");
        assertNotEquals(-1,appointmentDao.updateAppointment(appointment));
    }

    @After
    public void testDeleteAppointment() throws Exception {
        appointmentDao.addAppointment(appointment);
        assertFalse(appointmentDao.deleteAppointment(appointment));
    }

    @Test
    public void  getAllAppointmentsTest(){

        Appointment app = populateAppointment();
        appointmentDao.addAppointment(app);
        assertNotNull(appointmentDao.getAllAppointments().get(0));
    }

    private Appointment populateAppointment(){
        Appointment newAppointment = new Appointment();
        newAppointment.setId(1);
        newAppointment.setAppointment(new Date());
        newAppointment.setDescription("Health tests");
        newAppointment.setLocation("NUH Kent Ridge");
        return newAppointment;
    }

}
