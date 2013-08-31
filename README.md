## Airbrake Errors

This is a small project to play around with Scala. My goal is to write
an [Airbrake API](http://help.airbrake.io/kb/api-2/api-overview)
client and use it to get some additional insights into exceptions
tracked in Airbrake projects.

Some of the questions I might want to answer:

### Which exceptions happen most often?

Airbrake only shows the total number of notices since the first time
the exception ocurred. If an exception is old, it might have many
notices even though it only happens once a week while another
exception only has 50 notices, but they all happened within the last
few minutes.

### How did a deploy affect exceptions?

Are there exceptions that never happened before the release? Does some
particular error suddenly happen more often?
