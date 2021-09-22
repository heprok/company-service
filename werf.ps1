if ((Invoke-Expression -Command "multiwerf werf-path 1.2 ea" | Out-String -OutVariable WERF_PATH) -and ($LastExitCode -eq 0)) {
    multiwerf update 1.2 ea --in-background --output-file=~\.multiwerf\multiwerf_use_background_update.log --with-cache
} else {
    multiwerf update 1.2 ea
    Invoke-Expression -Command "multiwerf werf-path 1.2 ea" | Out-String -OutVariable WERF_PATH
}

function werf { & $WERF_PATH.Trim() $args }