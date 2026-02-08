from bottle import route, run
from CommandLineParser import parse_cli_options, load_config
import redis

args = parse_cli_options()
config = load_config(args.file)


@route('/')
def index():
    return "<b>Hello World!</b>!"


@route('/user')
def user():
    return config['user']


@route('/cowsay')
def cowsay():
    cowsay = config['server']['cowsay']
    return f"<b>Cow says {cowsay}</b>!"


@route('/config')
def config_endpoint():
    return config


if args.experimental:
    @route('/magic')
    def magic():
        return f"<b>You found me! <pre>--experimental</pre> features are great!</b>!"

if config['redis']['enabled']:
    r = redis.Redis(host=config['redis']['host'], port=config['redis']['port'], db=0)

    # To force connection on startup and fail-fast
    r.ping()


    @route('/put/<value>')
    def put_redis(value):
        r.lpush('mylist', value)
        return f"<b>You added {value} to the list</b>!"


    @route('/get')
    def get_redis():
        elements = r.lrange("mylist", 0, -1)
        return f"<b>Elements in the list: {elements}</b>!"

if __name__ == "__main__":
    run(host=config['server']['host'], port=config['server']['port'])
