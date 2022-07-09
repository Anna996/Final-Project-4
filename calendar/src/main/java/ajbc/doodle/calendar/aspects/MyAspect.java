package ajbc.doodle.calendar.aspects;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import ajbc.doodle.calendar.daos.DaoException;

@Aspect
@Component
public class MyAspect {
	
	@AfterThrowing(throwing = "ex", pointcut = "execution(* ajbc.doodle.calendar.daos.*.*(..))")
	public void convertToDaoException(Throwable ex) throws DaoException {
		throw new DaoException(ex.getMessage());
	}
}
