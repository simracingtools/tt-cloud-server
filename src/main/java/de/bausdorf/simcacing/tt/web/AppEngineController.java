package de.bausdorf.simcacing.tt.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AppEngineController {

	@GetMapping("/_ah/stop")
	public ResponseEntity<String> appEngineStopRequest() {
		log.warn("AppEngine send stop request");
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@GetMapping("/_ah/start")
	public ResponseEntity<String> appEngineStartRequest() {
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@GetMapping("/_ah/health")
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("Healthy", HttpStatus.OK);
	}
}
