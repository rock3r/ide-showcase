## Disabling errors

Open _Help_ > _Custom properties..._

```
idea.fatal.error.notification=disabled
idea.is.internal=false
```

**Note:** only works when [internal mode](https://plugins.jetbrains.com/docs/intellij/enabling-internal.html) is off.
If you want to keep internal mode, use instead:

```
fatal.error.icon.disable.blinking=true
```
