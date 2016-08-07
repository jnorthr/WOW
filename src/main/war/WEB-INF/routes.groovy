def stableDuration = 2.hour
def shortContentDuration = 2.minutes
def hotContentDuration = 10.minutes
def version = "1.0"

get "/",   forward: "/index.html",     cache: stableDuration
