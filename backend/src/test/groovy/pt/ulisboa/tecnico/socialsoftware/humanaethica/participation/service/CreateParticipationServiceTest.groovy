package groovy.pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.service;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import spock.lang.Unroll;
@DataJpaTest
public class CreateParticipationServiceTest extends SpockTest{

    def volunteer
    def setup(){
        volunteer = authUserService.loginDemoVolunteerAuth().getUser();


    }




    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

