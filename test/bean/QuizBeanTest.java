/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import bean.QuizBean;

public class QuizBeanTest {

    private QuizBean quizBean;

    @Before
    public void setUp() {
        quizBean = new QuizBean();
    }

    @Test
    public void testCheckAnswers_AllCorrect() {
        // Set correct answers
        quizBean.setQ1("A");
        quizBean.setQ2("D");
        quizBean.setQ3("B");
        quizBean.setQ4("A");
        quizBean.setQ5("D");

        // Call the method to check answers
        quizBean.checkAnswers();

        // Assert that the score is correct
        assertEquals("You scored 5/5.", quizBean.getResults());
    }

    @Test
    public void testCheckAnswers_SomeCorrect() {
        // Set some correct, some incorrect answers
        quizBean.setQ1("A");
        quizBean.setQ2("D");
        quizBean.setQ3("C"); // Incorrect
        quizBean.setQ4("A");
        quizBean.setQ5("B"); // Incorrect

        // Call the method to check answers
        quizBean.checkAnswers();

        // Assert that the score is correct
        assertEquals("You scored 3/5.", quizBean.getResults());
    }

    @Test
    public void testCheckAnswers_NoneCorrect() {
        // Set all incorrect answers
        quizBean.setQ1("B");
        quizBean.setQ2("B");
        quizBean.setQ3("A");
        quizBean.setQ4("C");
        quizBean.setQ5("A");

        // Call the method to check answers
        quizBean.checkAnswers();

        // Assert that the score is 0
        assertEquals("You scored 0/5.", quizBean.getResults());
    }
}
