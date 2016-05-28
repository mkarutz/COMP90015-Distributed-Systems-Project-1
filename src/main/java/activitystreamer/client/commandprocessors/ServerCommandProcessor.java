package activitystreamer.client.commandprocessors;

import activitystreamer.client.ClientSolution;
import activitystreamer.client.commandhandlers.*;
import activitystreamer.client.services.ClientReflectionService;
import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.core.shared.Connection;

public class ServerCommandProcessor extends CommandProcessor {
  public ServerCommandProcessor(ClientReflectionService rClientRefService, ClientSolution clientSolution) {
    super();

    handlers.add(new InvalidMessageCommandHandler());
    handlers.add(new ActivityBroadcastCommandHandler(rClientRefService));
    handlers.add(new LoginSuccessCommandHandler());
    handlers.add(new LoginFailedCommandHandler());
    handlers.add(new RedirectCommandHandler(clientSolution));
    handlers.add(new RegisterSuccessCommandHandler());
    handlers.add(new RegisterFailedCommandHandler());
  }

  @Override
  public void invalidMessage(Connection connection, Command command) {
    Command invalidCommand = new InvalidMessageCommand("Command type is invalid.");
    connection.pushCommand(invalidCommand);
    connection.close();
  }
}
