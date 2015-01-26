/*
 * Sonar Crowd Plugin
 * Copyright (C) 2009 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.plugins.crowd;

import com.atlassian.crowd.exception.*;
import com.atlassian.crowd.service.client.CrowdClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.security.Authenticator;

/**
 * @author Evgeny Mandrikov
 */
public class CrowdAuthenticator extends Authenticator {

	private static final Logger LOG = LoggerFactory.getLogger(CrowdAuthenticator.class);

	private final CrowdClient client;

	public CrowdAuthenticator(CrowdClient client) {
		this.client = client;
	}

	@Override
	public boolean doAuthenticate(Context context) {
		String login = context.getUsername();
		try {
			client.authenticateUser(login, context.getPassword());
			return true;
		} catch (UserNotFoundException e) {
			LOG.debug("User {} not found", login);
			return false;
		} catch (InactiveAccountException e) {
			LOG.debug("User {} is not active", login);
			return false;
		} catch (ExpiredCredentialException e) {
			LOG.debug("Credentials of user {} have expired", login);
			return false;
		} catch (ApplicationPermissionException e) {
			LOG.error("The application is not permitted to perform the requested operation" + " on the crowd server", e);
			return false;
		} catch (InvalidAuthenticationException e) {
			LOG.debug("Invalid credentials for user {}", login);
			return false;
		} catch (OperationFailedException e) {
			LOG.error("Unable to authenticate user " + login, e);
			return false;
		}
	}
}
