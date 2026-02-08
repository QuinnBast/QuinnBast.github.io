import argparse
import yaml
import os


def parse_cli_options():
    parser = argparse.ArgumentParser(
        description="An example Python REST api"
    )
    parser.add_argument(
        "--file",
        help="Configuration file to load settings from",
        default="exampleSettings.yaml",
    )
    parser.add_argument(
        "--experimental",
        help="Adds an experimental endpoint!",
        action='store_true'
    )

    args = parser.parse_args()
    print(args)
    return args


def load_config(configFile):
    with open(configFile, "r") as fp:
        config = yaml.safe_load(fp)

    # Also parse environment variables and add them to the config.
    config['user'] = dict()
    config['user']['firstName'] = os.getenv('API_USER_FIRST_NAME', default="Admin")
    config['user']['lastName'] = os.getenv('API_USER_LAST_NAME', default="Admin")
    config['user']['pet'] = os.getenv('API_USER_FAVORITE_PET', default="Dogs")

    print(config)
    return config
