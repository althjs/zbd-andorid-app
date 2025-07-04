#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#*****************************************************************************
#
#   Gradle start up script for UN*X
#
#*****************************************************************************

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"

while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done

APP_HOME=`dirname "$PRG"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS=("-Xmx64m" "-Xms64m")

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
case "`uname`" in
    CYGWIN*) cygwin=true ;;
    Darwin*) darwin=true ;;
esac

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
    [ -n "$APP_HOME" ] &&
        APP_HOME=`cygpath --unix "$APP_HOME"`
    [ -n "$JAVA_HOME" ] &&
        JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
fi

# Attempt to find java
if [ -z "$JAVA_HOME" ] ; then
    if $darwin ; then
        if [ -x '/usr/libexec/java_home' ] ; then
            JAVA_HOME=`/usr/libexec/java_home`
        elif [ -d "/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home" ]; then
            JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home"
        fi
    else
        java_path=`which java 2>/dev/null`
        if [ -n "$java_path" ] ; then
            java_path=`readlink -f "$java_path" 2>/dev/null`
            if [ -n "$java_path" ] ; then
                JAVA_HOME=`dirname "$java_path" 2>/dev/null`
                JAVA_HOME=`cd "$JAVA_HOME/.." && pwd`
            fi
        fi
    fi
fi
if [ -z "$JAVA_HOME" ] ; then
    die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.\n\nPlease set the JAVA_HOME variable in your environment to match the\nlocation of your Java installation."
fi

# Set-up variables
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add extension if not present
if [ -f "$APP_HOME/$APP_BASE_NAME.bat" ]; then
    APP_BASE_NAME="$APP_BASE_NAME.bat"
fi

# Use the maximum available, or set MAX_FD != -1 to use that value.
MAX_FD="maximum"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
    APP_HOME=`cygpath --path --windows "$APP_HOME"`
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
fi

# Split up the JVM options only if the variable is not quoted
if [ -z "${JVM_OPTS_QUOTED}" ]; then
    JVM_OPTS=($DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS)
else
    JVM_OPTS="$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS"
fi
JVM_OPTS[${#JVM_OPTS[*]}]="-Dorg.gradle.appname=$APP_BASE_NAME"

# Set the GRADLE_HOME directory
GRADLE_HOME="$APP_HOME/gradle"

# Set the CLASSPATH
CLASSPATH="$GRADLE_HOME/wrapper/gradle-wrapper.jar"

# Execute Gradle
exec "$JAVA_HOME/bin/java" "${JVM_OPTS[@]}" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"