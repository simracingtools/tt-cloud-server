package de.bausdorf.simcacing.tt.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppEngineController {

	@GetMapping("/_ah/start")
	public ResponseEntity<String> appEngineStartRequest() {
		return new ResponseEntity("OK", HttpStatus.OK);
	}

	@GetMapping("/_ah/health")
	public ResponseEntity<String> healthCheck() {
		return new ResponseEntity<>("Healthy", HttpStatus.OK);
	}
}
