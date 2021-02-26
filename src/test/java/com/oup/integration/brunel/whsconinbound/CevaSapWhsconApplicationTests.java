package com.oup.integration.brunel.whsconinbound;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.oup.integration.brunel.whsconinbound.routes.CevaRecadvToSapWhsconRoute;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
public class CevaSapWhsconApplicationTests {

	@Autowired
	private CamelContext camelContext;

	static final String OUT_URI = "mock:result";
	static final String IN_URI = "direct:start";

	@EndpointInject(uri = OUT_URI)
	protected MockEndpoint resultEndpoint;

	@EndpointInject(uri = IN_URI)
	protected ProducerTemplate template;

	@Before
	public void setup() throws Exception {
		camelContext.getRouteDefinition(CevaRecadvToSapWhsconRoute.class.getSimpleName()).adviceWith(camelContext,
				new AdviceWithRouteBuilder() {
					@Override
					public void configure() throws Exception {
						replaceFromWith(IN_URI);

						onException(Exception.class).log("in Exception Clause").to(OUT_URI);

						// don't backup files while running tests
						weaveById("backupXML").remove();
						weaveById("backupIDOC").remove();

						// replace FTP endpoint with mock endpoint
						weaveById("sendToSAP").replace().to(OUT_URI);
					}
				});
	}

	private File getInputFile(String name) throws URISyntaxException, IOException {
        return FileUtils.getFile("src", "test", "resources", name);
    }

	@Test
	@DirtiesContext
	public void testFileSuccessfullyProcessed() throws Exception {
		
		Exchange inEx = ExchangeBuilder.anExchange(camelContext).withBody(getInputFile("ceva_input.xml")).build();
		Exchange outEx = template.send(inEx);
		String resultBody= outEx.getIn().getBody(String.class);
		String sampleOutputPath = getClass().getClassLoader().getResource("output_tosap.xml").getPath();
		Diff xmlDiff = DiffBuilder.compare(Input.fromFile(sampleOutputPath))
									.withTest(Input.fromString(resultBody))
									.withAttributeFilter(attr -> !(attr.getName().equals("creationDate") 
																|| attr.getName().equals("creationTime")
																|| attr.getName().equals("NTANF")
																|| attr.getName().equals("NTANZ")))
									.build();
		
		System.out.println(xmlDiff);

		assertFalse(xmlDiff.hasDifferences());

	}
}
